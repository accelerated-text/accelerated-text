import { h }            from 'preact';

import GeneratorEditor  from '../generator-editor/GeneratorEditor';
import OutputPreview    from '../output-preview/OutputPreview';
import provideStore     from '../context/provide-store';

import S                from './AugmentedWriter.sass';
import store            from './store';


export default provideStore(
    'augmentedWriter', store,
)(({
    augmentedWriter: {
        onChangeXml,
        xml,
    },
}) =>
    <div className={ S.className }>
        <GeneratorEditor
            onChangeXml={ onChangeXml }
            xml={ xml }
        />
        <div>→</div>
        <OutputPreview xml={ xml } />
    </div>
);
