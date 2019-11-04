import logging
import json
import argparse
import socketserver
import subprocess
import tempfile

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
        result = subprocess.run("gf -make {}".format(grammar_path), shell=True)
        logger.info(result)
        


class GFHandler(BaseHTTPRequestHandler):
    def _set_headers(self):
        self.send_response(200)
        self.send_header("Content-type", "application/json")
        self.end_headers()

    def do_HEAD(self):
        self._set_headers()

    def do_POST(self):
        content_length = int(self.headers["Content-Length"])
        post_data = self.rfile.read(content_length).decode("UTF-8")
        logger.debug("Got: {}".format(post_data))
        compile_grammar(post_data)
        self._set_headers()
        
        self.wfile.write(response_json({"result": "NOTHING"}))



def main(args):
    with socketserver.TCPServer(("", args.port), GFHandler) as httpd:
        logger.info("Serving on port: {}".format(args.port))
        httpd.serve_forever()


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--port", default=8000, type=int, help="Server port")
    main(parser.parse_args())
