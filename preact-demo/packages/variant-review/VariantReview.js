import { h }            from 'preact';

import AtjReview        from '../atj-review/AtjReview';
import { useStores }    from '../vesa/';

import S                from './VariantReview.sass';


export default useStores([
    'planEditor',
    'variantsApi',
])(({
    planEditor: {
        documentPlan,
        workspaceXml,
    },
    variantsApi: {
        error,
        loading,
        result,
    },
}) =>
    <div className={ S.className }>
        <div className={ S.header }>
            [P]review
        </div>
        <div className={ S.body }>
            <div className={ S.item }>
                { workspaceXml ? workspaceXml : 'No Blockly yet.' }
            </div>
            <div className={ S.item }>
                { documentPlan ? JSON.stringify( documentPlan ) : 'No JSON yet.' }
            </div>
            { error &&
                <div className={ S.itemError }>
                    { error }
                </div>
            }
            { loading &&
                <div className={ S.item }>Loading variants...</div>
            }
            { result && (
                !( result.variants && result.variants.length )
                    ? <div className={ S.item }>No variants</div>
                    : result.variants.map( element =>
                        <div className={ S.item }>
                            <AtjReview key={ element.id } element={ element } />
                        </div>
                    )
            )}
        </div>
    </div>
);
