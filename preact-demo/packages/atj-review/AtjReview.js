import { h }            from 'preact';

import provideStore     from '../context/provide-store';

import AnnotatedText    from './AnnotatedText';
import S                from './AtjReview.sass';
import Sidebar          from './Sidebar';
import store            from './store';


export default provideStore(
    'atjReview', store,
)(({ element }) =>
    <div className={ S.className }>
        <AnnotatedText element={ element } />
        <Sidebar />
    </div>
);
