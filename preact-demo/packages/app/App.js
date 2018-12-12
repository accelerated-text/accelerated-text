import { h } from 'preact';

import AugmentedWriter from '../augmented-writer/AugmentedWriter';

import S from './App.sass';

export default () =>
    <div className={ S.className }>
        <div className={ S.header }>
            <img
                className={ S.logo }
                src="/augmented-writer-logo.png"
                title="Augmented Writer"
            />
        </div>
        <div className={ S.body }>
            <AugmentedWriter />
        </div>
    </div>;
