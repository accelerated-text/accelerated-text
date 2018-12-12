import { h }                from 'preact';

import GeneratorEditor      from '../generator-editor/GeneratorEditor';
import generatorEditorStore from '../generator-editor/store';
import OutputPreview        from '../output-preview/OutputPreview';
import provideStore         from '../context/provide-store';

import S                from './AugmentedWriter.sass';


export default provideStore(
    'generatorEditor', generatorEditorStore,
)(() =>
    <div className={ S.className }>
        <GeneratorEditor />
        <OutputPreview />
    </div>
);
