import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import { QA }               from '../tests/constants';

import EXAMPLE_XML          from './example.xml';
import S                    from './OnboardCode.sass';


export default class OnboardCode extends Component {

    static propTypes = {
        onCreateXml:        PropTypes.func.isRequired,
    };

    onClick = () => {
        this.props.onCreateXml( EXAMPLE_XML );
    };

    render = () =>
        <div className={ S.className }>
            <button
                children="Create a new document plan"
                className={ QA.ADD_EXAMPLE }
                onClick={ this.onClick }
            />
        </div>;
}
