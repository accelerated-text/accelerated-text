import { h, Component } from 'preact';

import Editor               from '../augmented-editor/AugmentedEditor';
import exampleGenerator     from '../example-generator/example-generator';
import OutputPreview        from '../output-preview/OutputPreview';

import Hints from './Hints';
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
        const { examples } = this.state;

        return (
            <div className={ S.className }>

                <div className={ S.documentActions }>
                    Document + actions
                </div>
                <div />
                <div className={ S.dataSetup }>
                    Add data source
                </div>

                <div />
                <div />
                <div>↓</div>

                <Editor onChangeTokens={ this.onChangeTokens } />
                <div>→</div>
                { examples
                    ? <OutputPreview examples={ examples } />
                    : <Hints />
                }
            </div>
        );
    }
}
