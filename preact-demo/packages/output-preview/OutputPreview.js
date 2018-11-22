import { h, Component } from 'preact';

import S from './OutputPreview.sass';

export default class OutputPreview extends Component {

    render() {
        return (
            <div className={ S.root }>
                <h2>Generated examples</h2>
            </div>
        );
    }
}
