import { h, Component } from 'preact';

import S from './WriterExamples.sass';

export default class WriterExamples extends Component {

    render() {
        return (
            <div className={ S.root }>
                <h2>Generated examples</h2>
            </div>
        );
    }
}
