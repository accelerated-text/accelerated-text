import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import planTemplate         from '../document-plans/rgl-plan-template';
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
                children="Create a new operation"
                className={ QA.ADD_EXAMPLE }
                onClick={ this.onClick }
            />
        </div>;
}
