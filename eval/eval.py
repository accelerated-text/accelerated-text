import os
import random
import argparse
import time

import acctext
import requests
import sacrebleu

from rouge import Rouge

from operator import itemgetter
from itertools import groupby

NLG_ENDPOINT = os.getenv("ACC_TEXT_URL", "http://localhost:3001")


def bleu_score(data):
    scores = []
    for refs, sys in data:
        bleu = sacrebleu.corpus_bleu([sys], [[ref] for ref in refs])
        scores.append(bleu.score)
    if scores:
        return sum(scores) / len(scores)
    else:
        return 0


def rouge_score(data):
    rouge = Rouge()
    scores = {'rouge-1': [], 'rouge-2': [], 'rouge-l': []}
    for refs, sys in data:
        for score in rouge.get_scores([sys] * len(refs), refs):
            scores['rouge-1'].append(score['rouge-1']['f'])
            scores['rouge-2'].append(score['rouge-2']['f'])
            scores['rouge-l'].append(score['rouge-l']['f'])
    n = len(scores['rouge-1'])
    if n > 0:
        return sum(scores['rouge-1']) / n, sum(scores['rouge-2']) / n, sum(scores['rouge-l']) / n
    else:
        return 0, 0, 0


def load_data(at, filename, ref='ref'):
    data = at.get_data_file(filename)
    items = []
    for row in data['rows']:
        row = dict(zip(data['header'], row))
        item = {"ref": row["ref"]}
        row.pop("ref")
        item["data"] = row
        items.append(item)
    items = sorted(items, key=lambda x: sorted(x['data'].items()))
    return [(k, [item["ref"] for item in group]) for k, group in groupby(items, key=itemgetter('data'))]


def wait_for_connection(at, timeout=60):
    connected = False
    while timeout > 0 and not connected:
        try:
            connected = at.health().get('health') == 'Ok'
        except requests.exceptions.ConnectionError:
            timeout -= 1
            time.sleep(1)
    if not connected:
        raise ConnectionError('Failed to connect to Accelerated Text backend.')


def main(args):
    at = acctext.AcceleratedText(host=NLG_ENDPOINT)
    strategy = args.strategy.upper()

    wait_for_connection(at)
    at.restore_state(args.state_file)
    items = load_data(at, args.data_file_name, ref=args.ref_column)

    idx = list(range(len(items)))
    random.Random(args.seed).shuffle(idx)
    idx = sorted(idx[:args.n])

    refs = {}
    data_rows = []
    for i in idx:
        data, ref = items[i]
        refs[i] = ref
        data_rows.append(data)

    results = {i: x['variants'] for i, x in zip(idx, at.generate_bulk(args.document_plan_name, data_rows))}
    for i, variants in results.items():
        print('%d:' % i)
        for variant in variants:
            print(variant)
        print()

    pairs = []
    if strategy == "RANDOM":
        pairs = list([(refs[k], random.Random(args.seed).choice(r))
                      for k, r in results.items()
                      if len(r) > 0])
    elif strategy == "ALL":
        pairs = list([(refs[k], item)
                      for k, r in results.items()
                      for item in r
                      if len(r) > 0])

    print('\n-----\n')

    bleu = bleu_score(pairs)
    print("BLEU score: {0:.4f}".format(bleu))
    assert bleu > 0
    rouge_1, rouge_2, rouge_l = rouge_score(pairs)
    assert rouge_1 > 0
    print("ROUGE-1 score: {0:.4f}".format(rouge_1))
    assert rouge_2 > 0
    print("ROUGE-2 score: {0:.4f}".format(rouge_2))
    assert rouge_l > 0
    print("ROUGE-L score: {0:.4f}".format(rouge_l))


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument(
        'document_plan_name',
        help='Name of the document plan to be evaluated',
    )
    parser.add_argument(
        'data_file_name',
        help='Name of the data file with `ref` column for reference',
    )
    parser.add_argument(
        'state_file',
        help='Accelerated Text state file containing document plan and data file used in evaluation',
    )
    parser.add_argument(
        "--ref_column",
        help="Column name containing text reference",
        type=int,
        default=10
    )
    parser.add_argument(
        "--n",
        help="Number of instances to generate",
        type=int,
        default=10
    )
    parser.add_argument(
        "--seed",
        help="Random seed for data shuffling",
        type=int,
        default=42)
    parser.add_argument(
        "--strategy",
        help="Choose strategy for eval. RANDOM - take random result from output and match with original. ALL - match all results with original, end result is basically an average",
        default="ALL")
    main(parser.parse_args())
