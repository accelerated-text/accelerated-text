import logging

from flask import Blueprint, request
from flask_restplus import Namespace, Resource, fields

from core import AVAILABLE_LANGS, DEFAULT_LANG, LANG_TO_MODEL
from core.analyser import (
    parse_sentences,
    parse_tokens,
    parse_entities,
    parse_combined
)

logger = logging.getLogger(__name__)

bp = Blueprint('analyser', __name__, url_prefix='/analyse')
ns = Namespace('analyser', description='Text Analysis API')

attribute = ns.model('attribute', {
    'key': fields.String(required=True),
    'value': fields.String(required=True)
})


annotation = ns.model('annotation', {
    'text': fields.String(required=True),
    'start': fields.Integer(required=True),
    'end': fields.Integer(required=True),
    'type': fields.String(
        required=True,
        description=('Defines type of result.'
                     'If it is an entity - returns label')
    ),
    'attributes': fields.List(fields.Nested(attribute))
})


nested_annotation = ns.model('nested_annotation', {
    'text': fields.String(required=True),
    'type': fields.String(
        required=True,
        description=('Define type of result')
    ),

    'children': fields.List(fields.Nested(annotation))
})

text = ns.model('text', {
    'text': fields.String(required=True, description='RAW text'),
    'lang': fields.String(enum=AVAILABLE_LANGS, default=DEFAULT_LANG)
})

text_w_custom_model = ns.model('text_with_custom_model', {
    'text': fields.String(required=True, description='RAW text'),
    'model_path': fields.String(required=True, description='Path to model')
})

parse_response = ns.model('parse_response', {
    'text': fields.String(required=True, description='Original text'),
    'results': fields.List(fields.Nested(annotation))
})

nested_response = ns.model('parse_response', {
    'text': fields.String(required=True, description='Original text'),
    'results': fields.List(fields.Nested(nested_annotation))
})


@ns.route('/sentence')
class SentenceParse(Resource):
    @ns.expect(text)
    @ns.marshal_with(parse_response)
    def post(self):
        content = request.json
        lang = content['lang']

        return {
            'text': content['text'],
            'results': parse_sentences(
                content['text'],
                LANG_TO_MODEL.get(lang, DEFAULT_LANG)
            )
        }


@ns.route('/tokens')
class TokensParse(Resource):
    @ns.expect(text)
    @ns.marshal_with(parse_response)
    def post(self):
        content = request.json
        lang = content['lang']
        return {
            'text': content['text'],
            'results': parse_tokens(
                content['text'],
                LANG_TO_MODEL.get(lang, DEFAULT_LANG)
            )
        }


@ns.route('/ner')
class NERParse(Resource):
    @ns.expect(text_w_custom_model)
    @ns.marshal_with(parse_response)
    def post(self):
        content = request.json
        model_path = content['model_path']
        return {
            'text': content['text'],
            'results': parse_entities(
                content['text'],
                model_path
            )
        }

@ns.route('/combined')
class CombinedParser(Resource):
    @ns.expect(text)
    #@ns.marshal_with(nested_response)
    def post(self):
        content = request.json
        lang = content['lang']
        return {
            'text': content['text'],
            'results': parse_combined(
                content['text'],
                LANG_TO_MODEL.get(lang, DEFAULT_LANG)
            )
        }
