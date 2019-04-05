import { h }            from 'preact';


export default ({ className, justIcon, message = 'Success' }) => {
    const text =        message && message.toString();

    return (
        <a
            className={ className }
            title={ text }
        >
            ✔️
            { justIcon ? null : [ ' ', text ] }
        </a>
    );
};
