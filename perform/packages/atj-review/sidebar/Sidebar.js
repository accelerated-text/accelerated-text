import { h }            from 'preact';

import { useStores }    from '../../vesa/';

import AnRef            from './AnRef';
import S                from './Sidebar.sass';


export default useStores([
    'atjReview',
])(({
    atjReview: {
        activeWord,
        activeAnnotation,
        activeReference,
        annotations,
        references,
    },
}) =>
    <div className={ S.className }>
        { activeWord && [
            <h4 className={ S.word }>
                { activeWord.text }
            </h4>,
            activeWord.annotations &&
                annotations
                    .filter( a => activeWord.annotations.includes( a.id ))
                    .map( a => <AnRef key={ a.id } element={ a } /> ),
            activeWord.references &&
                references
                    .filter( r => activeWord.references.includes( r.id ))
                    .map( r => <AnRef key={ r.id } element={ r } /> ),
        ]}
    </div>
);
