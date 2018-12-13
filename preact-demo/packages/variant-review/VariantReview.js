import { h }            from 'preact';

import useStores        from '../context/use-stores';

import S                from './VariantReview.sass';


export default useStores([
    'planEditor',
])(({
    planEditor: {
        workspaceXml,
    },
}) =>
    <div className={ S.className }>
        <div className={ S.header }>
            [P]review
        </div>
        <div className={ S.body }>
            { workspaceXml
                ? <div className={ S.example }>{ workspaceXml }</div>
                : <div>No examples yet.</div>
            }
        </div>
    </div>
);
