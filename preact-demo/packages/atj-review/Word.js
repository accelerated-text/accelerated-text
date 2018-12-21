import classnames       from 'classnames';
import { h, Component } from 'preact';

import { useStores }    from '../vesa/';

import S                from './Word.sass';


export default useStores([
    'atjReview',
])( class Word extends Component {

    onClick = () =>
        this.props.E.atjReview.onClickWord(
            this.props.element
        );

    render({
        atjReview: {
            activeAnnotation,
            activeReference,
            activeWord,
        },
        element: {
            id,
            text,
            annotations,
            references,
        },
    }) {
        const isHighlighted = (
            ( activeWord && activeWord.id === id )
            || ( activeAnnotation && annotations && annotations.includes( activeAnnotation ))
            || ( activeReference && references && references.includes( activeReference ))
        );

        const className =   classnames(
            S.className,
            isHighlighted && S.isHighlighted,
        );

        return (
            <span className={ className } onClick={ this.onClick }>
                { text }
            </span>
        );
    }
});
