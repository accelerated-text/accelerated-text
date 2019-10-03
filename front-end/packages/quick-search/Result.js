import classnames           from 'classnames';
import { h, Component }     from 'preact';

import getType              from './get-type';
import S                    from './Result.sass';


export default class QuickSearchResult extends Component {

    onClick = () => {
        this.props.onChoose && this.props.onChoose({
            ...this.props.result,
            selectedType:   this.props.selectedType,
        });
    };

    render({ className, isActive, result, selectedType }) {
        return (
            <div
                className={ classnames(
                    S.className,
                    isActive && S.isActive,
                    className,
                ) }
                onClick={ this.onClick }
            >
                <div
                    children={ getType( result, selectedType ).icon }
                    className={ S.icon }
                />
                <div
                    children={
                        result.types.length > 1
                            ? getType( result, selectedType + 1 ).icon
                            : null
                    }
                    className={ S.nextIcon }
                />
                <div className={ S.text }>
                    { result.text }
                    { result.details &&
                        <span
                            children={ result.details }
                            className={ S.details }
                        />
                    }
                </div>
            </div>
        );
    }
}
