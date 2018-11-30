import { h, Component } from 'preact';

import useStores        from '../context/use-stores';

import { QA }           from './qa.constants';


const OPTIONS = [
    'T-Shirts',
    'Hotels',
];


export default useStores([
    'generatorEditor',
])( class SelectContext extends Component {

    onChange = e =>
        this.props.generatorEditor.onChangeContext({
            contextName:    e.target.value,
        });

    render({
        generatorEditor: {
            contextName,
        },
    }) {
        return (
            <select
                className={ QA.SELECT_CONTEXT }
                onChange={ this.onChange }
                value={ contextName }
            >
                <option value="">select a context</option>
                { OPTIONS.map( name =>
                    <option key={ name } name={ name }>{ name }</option>
                )}
            </select>
        );
    }
});
