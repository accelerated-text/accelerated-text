import { h, Component } from 'preact';

import S from './OutputPreview.sass';

export default ({ examples }) =>

    <div className={ S.className }>
        <h2 className={ S.title }>Generated examples:</h2>
        { examples.map( str =>
            <div className={ S.example }>{ str }</div>
        )}
    </div>
