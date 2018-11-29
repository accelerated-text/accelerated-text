import { h, Component } from 'preact';

import useStores        from '../context/use-stores';


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
            <select onChange={ this.onChange }>
                <option value="">select a context</option>
                { OPTIONS.map(
                    name => (
                        <option
                            key={ name }
                            selected={ name === contextName }
                            value={ name }
                        >
                            { name }
                        </option>
                    )
                )}
            </select>
        );
    }
});
