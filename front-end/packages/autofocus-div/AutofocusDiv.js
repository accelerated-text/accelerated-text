import {
    h,
    Component,
    createRef,
}                           from 'preact';

import S                    from './AutofocusDiv.sass';


export default class AutofocusDiv extends Component {

        ref =               createRef();

        componentDidMount() {

            this.ref.current.focus();
        }

        render = props =>
            <div
                className={ S.className }
                { ...props }
                ref={ this.ref }
                tabIndex="0"
            />;
}
