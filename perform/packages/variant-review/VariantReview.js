import classnames           from 'classnames';
import { h }                from 'preact';

import AtjReview            from '../atj-review/AtjReview';
import { QA }               from '../tests/constants';
import VariantsView         from '../variants/ViewWithStatus';

import S                    from './VariantReview.sass';


export default () =>
    <VariantsView className={ S.className }>
        { ({ variants }) =>
            variants.map( element =>
                <div className={ classnames( S.item, QA.VARIANT ) }>
                    <AtjReview key={ element.id } element={ element } />
                </div>
            )
        }
    </VariantsView>;
