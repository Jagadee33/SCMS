import React from 'react';
import { cn } from '../utils/cn';

const Chart = ({ type = 'line', data = [], options = {}, className, ...props }) => {
  const renderChart = () => {
    switch (type) {
      case 'line':
        return (
          <div className={cn("w-full h-64", className)}>
            <svg viewBox="0 0 400 64" className="w-full h-full">
              {data.map((point, index) => (
                <g key={index}>
                  <circle
                    cx={point.x}
                    cy={point.y}
                    r="3"
                    fill="none"
                    stroke="#3b82f6"
                    strokeWidth="2"
                  />
                  {index > 0 && (
                    <line
                      x1={data[index - 1].x}
                      y1={data[index - 1].y}
                      x2={point.x}
                      y2={point.y}
                      stroke="#3b82f6"
                      strokeWidth="2"
                    />
                  )}
                </g>
              ))}
            </svg>
          </div>
        );
      
      case 'bar':
        return (
          <div className={cn("w-full h-64", className)}>
            <div className="flex items-end justify-around h-full">
              {data.map((item, index) => (
                <div
                  key={index}
                  className={cn(
                    "bg-blue-500 rounded-t",
                    item.color || "bg-blue-500"
                  )}
                  style={{
                    height: `${(item.value / Math.max(...data.map(d => d.value))) * 100}%`,
                    width: `${100 / data.length}%`
                  }}
                >
                  <div className="text-white text-xs font-medium p-2 text-center">
                    {item.label}
                  </div>
                </div>
              ))}
            </div>
          </div>
        );
      
      case 'pie':
        const total = data.reduce((sum, item) => sum + item.value, 0);
        const angles = data.map((item, index) => {
          const startAngle = index === 0 ? 0 : data.slice(0, index).reduce((sum, curr) => sum + curr.value, 0);
          return (startAngle / total) * 360 + (item.value / total) * 360;
        });
        
        return (
          <div className={cn("w-full h-64", className)}>
            <svg viewBox="0 0 42 42" className="w-full h-full">
              {data.map((item, index) => {
                const startAngle = angles[index];
                const endAngle = angles[index] + (item.value / total) * 360;
                const x1 = 21 + 20 * Math.cos((startAngle * Math.PI) / 180);
                const y1 = 21 + 20 * Math.sin((startAngle * Math.PI) / 180);
                const x2 = 21 + 20 * Math.cos((endAngle * Math.PI) / 180);
                const y2 = 21 + 20 * Math.sin((endAngle * Math.PI) / 180);
                const largeArcFlag = (item.value / total) > 0.5 ? 1 : 0;
                
                return (
                  <g key={index}>
                    <path
                      d={`M ${x1} ${y1} A 20 20 0 ${largeArcFlag} 1 ${x2} ${y2} Z`}
                      fill={item.color || "#3b82f6"}
                      stroke="white"
                      strokeWidth="2"
                    />
                    <text
                      x={x1 + (x2 - x1) / 2}
                      y={y1 + (y2 - y1) / 2}
                      fill="white"
                      fontSize="10"
                      textAnchor="middle"
                      dominantBaseline="middle"
                    >
                      {item.label}
                    </text>
                  </g>
                );
              })}
            </svg>
          </div>
        );
      
      default:
        return (
          <div className={cn("w-full h-64 flex items-center justify-center", className)}>
            <div className="text-gray-500">Unsupported chart type: {type}</div>
          </div>
        );
    }
  };

  return (
    <div className={cn("relative", className)} {...props}>
      {renderChart()}
    </div>
  );
};

Chart.displayName = "Chart";

export { Chart };
