import logging

from flask import Blueprint, request
from flask_restplus import Namespace, Resource, fields

from core import AVAILABLE_LANGS, DEFAULT_LANG
from core.thesaurus import get_thesaurus

logger = logging.getLogger(__name__)

bp = Blueprint('utils', __name__, url_prefix='/utils')
ns = Namespace('utils', description='Utility Functions')

synonym_request = ns.model('synonym_request', {
    'word': fields.String(required=True),
    'lang': fields.String(enum=AVAILABLE_LANGS, default=DEFAULT_LANG)
})

word_meaning = ns.model('word_meaning', {
    'key': fields.String(required=True),
    'pos': fields.String(required=True),
    'synonyms': fields.List(fields.String())
})

synonym_response = ns.model('synonym_response', {
    'results': fields.List(fields.Nested(word_meaning))
})


@ns.route('/synonym')
class SynonymFetcher(Resource):
    @ns.expect(synonym_request)
    @ns.marshal_with(synonym_response)
    def post(self):
        content = request.json
        lang = content['lang']

        return {
            'results': get_thesaurus(
                content['word'],
                lang
            )
        }
