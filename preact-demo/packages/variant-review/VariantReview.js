import { h }            from 'preact';

import AtjReview        from '../atj-review/AtjReview';
import TextLines        from '../text-lines/TextLines';
import useStores        from '../context/use-stores';

import S                from './VariantReview.sass';


export default useStores([
    'planEditor',
])(({
    planEditor: {
        gremlinCode,
        variants,
        variantsError,
        variantsLoading,
        workspaceXml,
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
            { variantsError &&
                <div className={ S.itemError }>
                    { variantsError }
                </div>
            }
            { variantsLoading &&
                <div className={ S.item }>Loading variants...</div>
            }
            { variants && (
                !( variants.variants && variants.variants.length )
                    ? <div className={ S.item }>No variants</div>
                    : variants.variants.map( element =>
                        <div className={ S.item }>
                            <AtjReview key={ element.id } element={ element } />
                        </div>
                    )
            )}
        </div>
    </div>
);
