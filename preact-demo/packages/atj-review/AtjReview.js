import { h }            from 'preact';

import provideStores    from '../vesa/provide-stores';

import AnnotatedText    from './AnnotatedText';
import atjReview        from './store';
import S                from './AtjReview.sass';
import Sidebar          from './sidebar/Sidebar';


export default provideStores({
    atjReview,
})(({ element }) =>
    <div className={ S.className }>
        <AnnotatedText element={ element } />
        <Sidebar />
    </div>
);
