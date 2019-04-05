import { h }            from 'preact';

import AnElement        from './AnElement';
import S                from './AnnotatedText.sass';


export default ({ element }) =>
    <div className={ S.className }>
        { element.children.map( child =>
            <AnElement key={ child.id } element={ child } />
        )}
    </div>;
