import { h }            from 'preact';

import Paragraph        from './Paragraph';
import Sentence         from './Sentence';
import TextBlock        from './TextBlock';
import Word             from './Word';


export default ({ element }) => (
    element.type === 'PARAGRAPH'
        ? <Paragraph element={ element } />
    : element.type === 'SENTENCE'
        ? <Sentence element={ element } />
    : element.text
        ? <Word element={ element } />
    : ( element.children && element.children.length )
        ? <TextBlock element={ element } />
        : null
);
