import logging

from flask import Flask
from flask_restplus import Api, Resource
from flask_cors import CORS

from api.routes import analyser


def register_endpoint(app, api, mod):
    app.register_blueprint(mod.bp)
    api.add_namespace(mod.ns)


def create_app():
    logging.basicConfig(level=logging.INFO)
    app = Flask(__name__)
    CORS(app, resources={r'/analyser/*': {'origins': '*'}})
    api = Api(
        app,
        title='Root API',
        version='1.0'
    )

    @api.route('/health')
    class Health(Resource):
        def get(self):
            return {'status': 'OK'}

    register_endpoint(app, api, analyser)
    return app


if __name__ == '__main__':
    app = create_app()
    app.run(debug=True)
