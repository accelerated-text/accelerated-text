import { h } from 'preact';

import Editor               from '../augmented-editor/AugmentedEditor';
import OutputPreview        from '../output-preview/OutputPreview';

import S from './AugmentedWriter.sass';

export default () =>
    <div className={ S.className }>

        <div className={ S.documentActions }>
            Document + actions
        </div>
        <div />
        <div className={ S.dataSetup }>
            Add data source
        </div>

        <div />
        <div />
        <div>↓</div>

        <Editor />
        <div>→</div>
        <OutputPreview />
    </div>;
