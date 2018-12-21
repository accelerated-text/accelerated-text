import { h }            from 'preact';

import AtjReview        from '../atj-review/AtjReview';
import TextLines        from '../text-lines/TextLines';
import { useStores }    from '../vesa/';

import S                from './VariantReview.sass';


export default useStores([
    'planEditor',
    'variantsApi',
])(({
    planEditor: {
        gremlinCode,
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
                <TextLines
                    text={ gremlinCode ? gremlinCode : 'No Gremlin code yet.' }
                />
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
