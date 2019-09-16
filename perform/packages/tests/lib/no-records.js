import { EMPTY_CONCEPTS }       from '../data/concepts';
import { EMPTY_DATA_FILE_LIST } from '../data/data-file-list';
import { EMPTY_DOCUMENT_PLANS } from '../data/document-plan-list';
import { EMPTY_DICTIONARY }     from '../data/dictionary';
import { EMPTY_RFLAGS }         from '../data/reader-flags';

import openPageWithResponses    from './open-page-with-responses';


export default openPageWithResponses({
    concepts:               EMPTY_CONCEPTS,
    dataFiles:              EMPTY_DATA_FILE_LIST,
    dictionary:             EMPTY_DICTIONARY,
    documentPlans:          EMPTY_DOCUMENT_PLANS,
    nlgJob:                 null,
    nlgJobResult:           null,
    readerFlags:            EMPTY_RFLAGS,
});
