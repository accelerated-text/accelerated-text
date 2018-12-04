import classnames       from 'classnames';
import { h }            from 'preact';

import ABlock           from '../blocks/ABlock';
import BlocklyEditor    from '../blockly-editor/BlocklyEditor';
import provideStore     from '../context/provide-store';

import Header           from './Header';
import OnboardCode      from './OnboardCode';
import OnboardData      from './OnboardData';
import { QA }           from './qa.constants';
import S                from './GeneratorEditor.sass';
import store            from './store';


export default provideStore(
    'generatorEditor', store,
)(({
    generatorEditor: {
        blocks,
    },
}) =>
    <div className={ S.className }>
        <Header className={ QA.HEADER } />
        <div className={ classnames( S.body, QA.BODY ) }>
            <OnboardData>
                <OnboardCode>
                    { blocks && blocks.length &&
                        blocks.map( block => <ABlock block={ block } /> )
                    }
                    <BlocklyEditor />
                </OnboardCode>
            </OnboardData>
        </div>
    </div>
);
