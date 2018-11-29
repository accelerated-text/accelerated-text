import { h, Component } from 'preact';

import ABlock           from '../blocks/ABlock';
import provideStore     from '../context/provide-store';

import Header           from './Header';
import OnboardData      from './OnboardData';
import S                from './GeneratorEditor.sass';
import store            from './store';


export default provideStore(
    'generatorEditor', store,
)(
    class GeneratorEditor extends Component {

        onChangeContext = e =>
            this.props.generatorEditor.onChangeContext({
                contextName:    e.target.value,
            });

        render({ generatorEditor: {
            children,
            contextName,
            dataSample,
        }}) {
            return (
                <div className={ S.className }>
                    <div className={ S.head }>
                        <Header />
                    </div>
                    <div className={ S.body }>
                        <OnboardData>
                            next step
                            { children.map( child => <ABlock block={ child } /> )}
                        </OnboardData>
                    </div>
                </div>
            );
        }
    });
