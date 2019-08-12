import { path }             from 'ramda';

export default ( result, rowNum = 0 ) =>
    path(
        [
            'variants', 0,
            'children', 0,
            'children', rowNum ? rowNum : 0,
            'text',
        ],
        result,
    );
