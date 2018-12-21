import { h }            from 'preact';

import { mount }        from '../vesa/';

import AnnotatedText    from './AnnotatedText';
import atjReview        from './store';
import S                from './AtjReview.sass';
import Sidebar          from './sidebar/Sidebar';


export default mount({
    atjReview,
})(({ element }) =>
    <div className={ S.className }>
        <AnnotatedText element={ element } />
        <Sidebar />
    </div>
);
