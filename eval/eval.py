import sys
import os
import csv
import random
import argparse

import requests

# Hack to add custom path
sys.path.append("e2e-metrics")

from itertools import dropwhile, takewhile, groupby

from metrics.pymteval import BLEUScore

DOCUMENT_PLAN_NAME=os.getenv("DOCUMENT_PLAN_NAME", "Restaurants")
NLG_ENDPOINT="{}/nlg".format(os.getenv("ACC_TEXT_URL", "http://localhost:3001"))


def bleu_score(data):
    bleu = BLEUScore()
    for ref, base in data:
        bleu.append(base, ref)

    return bleu.score()


def not_empty_line(x):
    return x != "\n"

def generate_results(data, document_plan_name):
    req = {
        "documentPlanName": document_plan_name,
        "readerFlagValues": {"Eng": True},
        "dataRows": data
    }

    resp = requests.post("{url}/_bulk/".format(url=NLG_ENDPOINT), json=req)
    results = {}
    for result_id in resp.json()["resultIds"]:
        result = requests.get("{url}/{result_id}?format=raw".format(
            url=NLG_ENDPOINT,
            result_id=result_id
        )).json()
        results[result_id] = result["variants"]
    print("Results: {}".format(results))
    return results

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


def main(args):
    strategy = args.strategy.upper()
    ref = []
    data_rows = {}

    items = list(group_data(load_data()))

    for idx, (data, refs) in enumerate(items[:10]):
        ref.append(refs)
        data_rows[idx] = data

    results = dict(generate_results(data_rows, DOCUMENT_PLAN_NAME))

    if strategy == "RANDOM":
        pairs = list([(ref[int(k)], random.choice(r))
                      for k, r in results.items()
                      if len(r) > 0])

    elif strategy == "ALL":
        pairs = list([(ref[int(k)], item)
                      for k, r in results.items()
                      for item in r
                      if len(r) > 0])

    score = bleu_score(pairs)
    print("BLEU score: {0:.4f}".format(score))


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--strategy",
        help="Choose strategy for eval. RANDOM - take random result from output and match with original. ALL - match all results with original, end result is basically an average",
        default="RANDOM")
    main(parser.parse_args())
