import { h }            from 'preact';

import useStores        from '../context/use-stores';

import S                from './OutputPreview.sass';


export default useStores([
    'generatorEditor',
])(({
    generatorEditor: {
        workspaceXml,
    },
}) =>
    <div className={ S.className }>
        { workspaceXml
            ? (
                <div>
                    <h2 className={ S.title }>Generated examples:</h2>
                    <div className={ S.example }>{ workspaceXml }</div>
                </div>
            )
            : <div>No examples yet.</div>
        }
    </div>
);
