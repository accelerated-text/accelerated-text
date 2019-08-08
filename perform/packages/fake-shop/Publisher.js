import { h }                from 'preact';

import S                    from './Publisher.sass';


export default () =>
    <div className={ S.className }>
        <h3 className={ S.title }>Publish a book to Shopify</h3>
        <label>
            Shop URL:
            <input />
        </label>
        <label>
            Shopify API key:
            <input />
        </label>
        <label>
            ISBN:
            <input />
        </label>
        <button children="Publish" />
    </div>;
