import { h } from 'preact';

import S from './WriterInput.sass';

export default () =>
    <textarea
        className={ S.root }
        placeholder="Enter exapmle text"
    />;
