import classnames       from 'classnames';
import { h }            from 'preact';

import BlocklyEditor    from '../blockly-editor/BlocklyEditor';
import useStores        from '../context/use-stores';

import Header           from './Header';
import OnboardCode      from './onboard/Code';
import OnboardData      from './onboard/Data';
import { QA }           from './qa.constants';
import S                from './GeneratorEditor.sass';


export default useStores([
    'generatorEditor',
])(({
    generatorEditor: {
        blocklyXml,
        onChangeBlocklyWorkspace,
    },
}) =>
    <div className={ S.className }>
        <Header className={ QA.HEADER } />
        <div className={ classnames( S.body, QA.BODY ) }>
            <OnboardData>
                <OnboardCode>
                    { blocklyXml &&
                        <BlocklyEditor
                            onChangeWorkspace={ onChangeBlocklyWorkspace }
                            workspaceXml={ blocklyXml }
                        />
                    }
                </OnboardCode>
            </OnboardData>
        </div>
    </div>
);
