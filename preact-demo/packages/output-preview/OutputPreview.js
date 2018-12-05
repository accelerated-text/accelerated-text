import { h }            from 'preact';

import useStores        from '../context/use-stores';

import S                from './OutputPreview.sass';


export default useStores([
    'generatorEditor',
])(({
    generatorEditor: {
        blocklyXml,
    },
}) =>
    <div className={ S.className }>
        { blocklyXml
            ? (
                <div>
                    <h2 className={ S.title }>Generated examples:</h2>
                    <div className={ S.example }>{ blocklyXml }</div>
                </div>
            )
            : <div>No examples yet.</div>
        }
    </div>
);
