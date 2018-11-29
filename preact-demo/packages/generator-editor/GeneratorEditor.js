import { h, Component } from 'preact';

import ABlock           from '../blocks/ABlock';
import provideStore     from '../context/provide-store';

import Header           from './Header';
import S                from './GeneratorEditor.sass';
import SelectContext    from './SelectContext';
import store            from './store';
import UploadDataSample from './UploadDataSample';


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
                        { !dataSample &&
                            <div className={ S.onboardData }>
                                { 'Please ' }
                                <UploadDataSample />
                                { ' a data sample CSV.' }
                            </div>
                        }
                        { !contextName &&
                            <div className={ S.onboardContext }>
                                { 'Please ' }
                                <SelectContext />
                            </div>
                        }
                        { children.map( child => <ABlock block={ child } /> )}
                    </div>
                </div>
            );
        }
    });
