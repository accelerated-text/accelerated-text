import { h }                from 'preact';

import Router               from 'preact-router';

import AcceleratedText      from '../accelerated-text/AcceleratedText';
import RGL                  from '../rgl/RGL';
import AMR                  from '../amr/AMR';

const Error = ({ type, url }) => (
	<section class="error">
		<h2>Error {type}</h2>
		<pre>{url}</pre>
	</section>
);


export default () => (
    <Router>
        <AcceleratedText path="/"/>
        <RGL path="/rgl" />
        <AMR path="/amr" />
        <Error type="404" default />
    </Router>
);