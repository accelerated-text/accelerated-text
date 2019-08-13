import { h }                from 'preact';

import {
    Error,
    Info,
    Loading,
}                           from '../ui-messages/';
import { useStores }        from '../vesa/';


export default useStores([
    'variantsApi',
])(({
    children,
    className,
    emptyMessage =          'No variants.',
    loadingMessage =        'Loading variants...',
    variantsApi: {
        error,
        loading,
        result,
    },
}) =>
    <div className={ className }>
        { error
            ? <Error message={ error } />
        : loading
            ? <Loading message={ loadingMessage } />
        : ( result && result.variants && result.variants.length )
            ? children({
                variants:   result.variants,
            })
            : <Info message={ emptyMessage } />
        }
    </div>
);
