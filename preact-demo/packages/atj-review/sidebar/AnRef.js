import classnames       from 'classnames';
import { h, Component } from 'preact';

import useStores        from '../../vesa/use-stores';

import S                from './AnRef.sass';


const TYPE_LABELS = {
    _EXAMPLE_PROPERTY_LIST_:    'Property list',
    _EXAMPLE_CALL_TO_ACTION_:   'Call to action',
};


export default useStores([
    'atjReview',
])( class AnRef extends Component {

    onClick = () => {
        const { atjReview, E, element } = this.props;

        atjReview.annotations.includes( element )
            && E.atjReview.onClickAnnotation(
                element.id === atjReview.activeAnnotation
                    ? null
                    : element.id
            );

        atjReview.references.includes( element )
            && E.atjReview.onClickReference(
                element.id === atjReview.activeReference
                    ? null
                    : element.id
            );
    }

    render({
        atjReview: { activeAnnotation, activeReference },
        element: { id, type },
    }) {

        const isHighlighted = (
            activeAnnotation === id
            || activeReference === id
        );

        const className = classnames(
            S.className,
            isHighlighted && S.isHighlighted,
        );

        return (
            <div className={ className } onClick={ this.onClick }>
                { TYPE_LABELS[type] }
            </div>
        );
    }
});
