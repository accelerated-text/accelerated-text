import logging
import json
import argparse
import socketserver
import subprocess
import tempfile

import pgf

from http.server import BaseHTTPRequestHandler

logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)


def setup_headers(fn):
    def wrapper(self, *args, **kwargs):
        self.send_response(200)
        self.send_header("Content-type", "application/json")
        self.end_headers()
        return fn(self, *args, **kwargs)
    return wrapper


def json_response(fn):
    def wrapper(self, *args, **kwargs):
        logger.info("Doing json response")
        result = fn(self, *args, **kwargs)
        self.wfile.write(json.dumps(result).encode("UTF-8"))
    return wrapper


def json_request(fn):
    def wrapper(self, *args, **kwargs):
        content_length = int(self.headers["Content-Length"])
        post_data = json.loads(self.rfile.read(content_length).decode("UTF-8"))
        logger.debug("Got: {}".format(post_data))
        return fn(self, post_data, *args, **kwargs)
    return wrapper


def compile_grammar(raw):
    with tempfile.TemporaryDirectory() as tmpdir:
        logger.info("Created temp dir: {}".format(tmpdir))
        grammar_path = "{}/grammar.cf".format(tmpdir)
        with open(grammar_path, "w") as f:
            logger.info("Wrote tmp file: {}".format(grammar_path))
            f.write(raw)

        logger.info("Compiling")
        proc = subprocess.Popen(
            "gf --output-dir={1} -make {0}".format(grammar_path, tmpdir),
            shell=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE
        )
        (result, error) = proc.communicate()

        if proc.returncode != 0:
            logger.error(error)
            return None
        else:
            logger.debug("Compiled successfuly! Message: {}".format(result))
            grammar = pgf.readPGF("{0}/grammarAbs.pgf".format(tmpdir))
            return grammar


class GFHandler(BaseHTTPRequestHandler):
    @setup_headers
    def do_HEAD(self):
        pass

    @json_response
    @setup_headers
    @json_request
    def do_POST(self, data):
        grammar = compile_grammar(data["content"])
        if grammar:
            logger.info("Generating")
            results = []
            try:
                logger.debug("Start category: {}".format(grammar.startCat))
                expressions = grammar.generateAll(grammar.startCat)
                logger.debug("Expressions: {}".format(list(expressions)))
                logger.debug("Languages: {}".format(grammar.languages))
                lang = grammar.languages["grammar"]
                results = list([r
                                for (_, e) in expressions
                                for r in lang.linearizeAll(e)])
            except Exception as ex:
                logger.exception(ex)

            logger.debug("Results: {}".format(results))
            return {"results": results}
        else:
            logger.info("No grammar")
            return {"results": []}




def main(args):
    httpd = socketserver.TCPServer(("", args.port), GFHandler)
    logger.info("Serving on port: {}".format(args.port))
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        logger.info("Stopping server")
        httpd.server_close()


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--port", default=8000, type=int, help="Server port")
    main(parser.parse_args())
