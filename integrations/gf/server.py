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

def response_json(content):
    return json.dumps(content).encode("UTF-8")


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
            logger.info("Compiled successfuly! Message: {}".format(result))
            grammar = pgf.readPGF("{0}/grammarAbs.pgf".format(tmpdir))
            return grammar
        


class GFHandler(BaseHTTPRequestHandler):
    def _set_headers(self):
        self.send_response(200)
        self.send_header("Content-type", "application/json")
        self.end_headers()

    def do_HEAD(self):
        self._set_headers()

    def do_POST(self):
        content_length = int(self.headers["Content-Length"])
        post_data = json.loads(self.rfile.read(content_length).decode("UTF-8"))
        logger.debug("Got: {}".format(post_data))
        grammar = compile_grammar(post_data["content"])
        self._set_headers()
        if grammar:
            expressions = grammar.generateAll(grammar.startCat)
            lang = grammar.languages["grammar"]
            results = list([r
                            for (_, e) in expressions
                            for r in lang.linearizeAll(e)])
            logger.debug("Results: {}".format(results))
            self.wfile.write(response_json({"results": results}))
        else:
            self.wfile.write(response_json({"results": []}))




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
