import { h }            from 'preact';

import AtjReview        from '../atj-review/AtjReview';
import {
    Error,
    Info,
    Loading,
}   from '../ui-messages/';
import { useStores }    from '../vesa/';

import S                from './VariantReview.sass';


export default useStores([
    'variantsApi',
])(({
    variantsApi: {
        error,
        loading,
        result,
    },
}) =>
    <div className={ S.className }>
        { error &&
            <Error className={ S.itemError } message={ error } />
        }
        { loading &&
            <Loading className={ S.item } message="Loading variants..." />
        }
        { result && (
            !( result.variants && result.variants.length )
                ? <Info className={ S.item } message="No variants" />
                : result.variants.map( element =>
                    <div className={ S.item }>
                        <AtjReview key={ element.id } element={ element } />
                    </div>
                )
        )}
    </div>
);
