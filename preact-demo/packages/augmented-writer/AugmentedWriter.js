import { h, Component } from 'preact';

import exampleGenerator     from '../example-generator/example-generator';
import GeneratorEditor      from '../generator-editor/GeneratorEditor';
import OutputPreview        from '../output-preview/OutputPreview';

import S from './AugmentedWriter.sass';

export default class AugmentedWriter extends Component {

    state = {
        tokens:         null,
        examples:       null,
    };

    onChangeTokens = async tokens => {

        this.setState({ tokens });

        const example = await exampleGenerator( tokens );

        this.setState({
            examples:   [ example ],
        });
    }

    render() {
        return (
            <div className={ S.className }>
                <GeneratorEditor onChangeTokens={ this.onChangeTokens } />
                <div>→</div>
                <OutputPreview examples={ this.state.examples } />
            </div>
        );
    }
}
