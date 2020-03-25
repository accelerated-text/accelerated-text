import sys
import os
import csv
import random

import requests

# Hack to add custom path
sys.path.append("e2e-metrics")

from itertools import dropwhile, takewhile, groupby

from gql import gql, Client
from gql.transport.requests import RequestsHTTPTransport

from metrics.pymteval import BLEUScore

DOCUMENT_PLAN_NAME=os.getenv("DOCUMENT_PLAN_NAME", "Restaurants")
GQL_ENDPOINT=os.getenv("GRAPH_QL_URL", "http://localhost:3001/_graphql")
NLG_ENDPOINT="{}/nlg".format(os.getenv("ACC_TEXT_URL", "http://localhost:3001"))


def get_document_plans():
    transport = RequestsHTTPTransport(
        url=GQL_ENDPOINT,
        use_json=True,
        headers={
            "Content-type": "application/json",
        },
        verify=False
    )

    client = Client(
        retries=3,
        transport=transport,
        fetch_schema_from_transport=True,
    )

    query = gql("{documentPlans{items{id name}}}")
    results = client.execute(query)
    return dict([(item["name"], item["id"]) for item in results["documentPlans"]["items"]])


def bleu_score(data):
    bleu = BLEUScore()
    for ref, base in data:
        bleu.append(base, ref)

    return bleu.score()


def not_empty_line(x):
    return x != "\n"

def generate_results(data, document_plan_id):
    req = {
        "documentPlanId": document_plan_id,
        "readerFlagValues": {"English": True},
        "dataRows": data,
        "enrich": True
    }

    resp = requests.post("{url}/_bulk/".format(url=NLG_ENDPOINT), json=req)
    result_id = resp.json()["resultId"]
    print("ResultId: {}".format(result_id))

    results = requests.get("{url}/{result_id}?format=raw".format(
        url=NLG_ENDPOINT,
        result_id=result_id
    )).json()

    print("Results: {}".format(results))

    return results["variants"]

def load_data():
    with open("data/devset.csv", "r") as f:
        reader = csv.DictReader(f)
        for row in reader:
            item = {"ref": row["ref"]}
            row.pop("ref")
            item["data"] = row
            yield item

def group_data(data):
    return [(k, list([item["ref"] for item in group]))
            for k, group in groupby(data, key=lambda x: x["data"])]


if __name__ == "__main__":
    document_plans = get_document_plans()
    print("Available Document plans: {}".format(document_plans))
    document_plan_id = document_plans.get(DOCUMENT_PLAN_NAME, None)

    ref = []
    data_rows = {}

    items = list(group_data(load_data()))

    for idx, (data, refs) in enumerate(items):
        ref.append(refs)
        data_rows[idx] = data

    results = dict(generate_results(data_rows, document_plan_id))

    original_pairs = list([(ref[int(k)], random.choice(r)["original"])
                           for k, r in results.items()
                           if len(r) > 0])

    score = bleu_score(original_pairs)
    print("original BLEU score: {0:.4f}".format(score))

    enriched_pairs = list([(ref[int(k)], random.choice([(v["enriched"] if "enriched" in v else v["original"])
                                                        for v in r]))
                           for k, r in results.items()
                           if len(r) > 0])


    score = bleu_score(enriched_pairs)
    print("enriched BLEU score: {0:.4f}".format(score))
