import { path }             from 'ramda';

export default ( result, rowNum = 0 ) =>
    path(
        [
            'variants', rowNum ? rowNum : 0,
            'children', 0,
            'text',
        ],
        result,
    );
