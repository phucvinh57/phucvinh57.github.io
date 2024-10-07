export function Contact() {
	// Github: phucvinh57
	// Facebook: npvinh.0507
	// Mail: npvinh0507@gmail.com
	return (
		<div className="flex gap-2">
			<a
				className="btn btn-xs"
				href={"https://github.com/phucvinh57"}
				target="_blank"
			>
				<img src="/github.png" className="w-4 h-4" />
			</a>
			<a
				className="btn btn-xs"
				href={"https://facebook.com/npvinh.0507"}
				target="_blank"
			>
				<img src="/facebook.png" className="w-4 h-4" />
			</a>
			<a
				className="btn btn-xs"
				href={"mailto:npvinh0507@gmail.com"}
				target="_blank"
			>
				<img src="/mail.png" className="w-4 h-4" />
			</a>
		</div>
	);
}
