import { h } from 'preact';

import Editor               from '../augmented-editor/AugmentedEditor';
import OutputPreview        from '../output-preview/OutputPreview';

import S from './AugmentedWriter.sass';

export default () =>
    <div className={ S.className }>
        <Editor />
        <OutputPreview />
    </div>;
