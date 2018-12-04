import { h } from 'preact';

import S from './OutputPreview.sass';

export default ({ examples }) =>

    <div className={ S.className }>
        { ( examples && examples.length )
            ? (
                <div>
                    <h2 className={ S.title }>Generated examples:</h2>
                    { examples.map( str =>
                        <div className={ S.example }>{ str }</div>
                    )}
                </div>
            )
            : <div>No examples yet.</div>
        }
    </div>;
