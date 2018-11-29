import { h }            from 'preact';

import ABlock           from '../blocks/ABlock';
import provideStore     from '../context/provide-store';

import Header           from './Header';
import OnboardCode      from './OnboardCode';
import OnboardData      from './OnboardData';
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
        <Header />
        <div className={ S.body }>
            <OnboardData>
                <OnboardCode>
                    { blocks && blocks.length &&
                        blocks.map( block => <ABlock block={ block } /> )
                    }
                </OnboardCode>
            </OnboardData>
        </div>
    </div>
);
