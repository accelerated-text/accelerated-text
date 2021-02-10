import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import planTemplate         from '../document-plans/paradigms-plan-template';
import { QA }               from '../tests/constants';

import S                    from './OnboardCode.sass';


export default class OnboardCode extends Component {

    static propTypes = {
        onCreateXml:        PropTypes.func.isRequired,
    };

    onClick = () => {
        this.props.onCreateXml( planTemplate.blocklyXml );
    };

    render = () =>
        <div className={ S.className }>
            <button
                children="Create a new Abstract Meaning Representation"
                className={ QA.ADD_EXAMPLE }
                onClick={ this.onClick }
            />
        </div>;
}
