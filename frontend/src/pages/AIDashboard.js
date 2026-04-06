import React, { useState, useEffect } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { Alert, AlertDescription } from '../components/ui/alert';
import { Badge } from '../components/ui/badge';
import { Progress } from '../components/ui/progress';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../components/ui/tabs';
import { Chart } from '../components/ui/chart';

const AIDashboard = () => {
  const [activeTab, setActiveTab] = useState('overview');
  const [loading, setLoading] = useState(true);
  const [performanceData, setPerformanceData] = useState(null);
  const [atRiskStudents, setAtRiskStudents] = useState([]);
  const [insights, setInsights] = useState(null);
  const [error, setError] = useState(null);

  // Mock data for demonstration
  const mockPerformanceData = {
    studentId: 1,
    studentName: "John Doe",
    currentGPA: 3.4,
    attendanceRate: 87.5,
    gradeTrend: 0.2,
    attendanceTrend: 2.1,
    riskLevel: "MEDIUM",
    predictions: {
      nextSemesterGPA: 3.6,
      nextSemesterAttendance: 89.5,
      graduationProbability: 0.92,
      honorRollProbability: 0.75,
      academicWarningProbability: 0.15
    },
    recommendations: [
      "Continue maintaining consistent study habits",
      "Consider joining study groups for challenging courses",
      "Maintain current attendance pattern"
    ],
    generatedAt: new Date().toISOString()
  };

  const mockAtRiskStudents = [
    {
      studentId: 2,
      studentName: "Jane Smith",
      currentGPA: 1.8,
      attendanceRate: 65.0,
      riskLevel: "HIGH",
      gradeTrend: -0.3,
      attendanceTrend: -5.2,
      recommendations: [
        "Schedule regular tutoring sessions",
        "Meet with academic advisor",
        "Improve attendance to meet requirements"
      ]
    },
    {
      studentId: 3,
      studentName: "Mike Johnson",
      currentGPA: 2.1,
      attendanceRate: 72.0,
      riskLevel: "HIGH",
      gradeTrend: -0.1,
      attendanceTrend: -2.8,
      recommendations: [
        "Review study habits and time management",
        "Seek help from professors early",
        "Set up automatic class reminders"
      ]
    }
  ];

  useEffect(() => {
    // Simulate API calls
    setTimeout(() => {
      setPerformanceData(mockPerformanceData);
      setAtRiskStudents(mockAtRiskStudents);
      setInsights(mockPerformanceData);
      setLoading(false);
    }, 1000);
  }, []);

  const getRiskLevelColor = (level) => {
    switch (level) {
      case 'LOW': return 'bg-green-100 text-green-800';
      case 'MEDIUM': return 'bg-yellow-100 text-yellow-800';
      case 'HIGH': return 'bg-orange-100 text-orange-800';
      case 'CRITICAL': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const getRiskLevelIcon = (level) => {
    switch (level) {
      case 'LOW': return '🟢';
      case 'MEDIUM': return '🟡';
      case 'HIGH': return '🟠';
      case 'CRITICAL': return '🔴';
      default: return '⚪';
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-lg text-gray-600">Loading AI Analytics...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-7xl mx-auto">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">🤖 AI/ML Analytics Dashboard</h1>
          <p className="text-gray-600">Intelligent insights and predictions for academic performance</p>
        </div>

        {error && (
          <Alert className="mb-6 border-red-200 bg-red-50">
            <AlertDescription className="text-red-800">
              {error}
            </AlertDescription>
          </Alert>
        )}

        <Tabs value={activeTab} onValueChange={setActiveTab} className="space-y-6">
          <TabsList className="grid w-full grid-cols-4">
            <TabsTrigger value="overview">Performance Overview</TabsTrigger>
            <TabsTrigger value="predictions">AI Predictions</TabsTrigger>
            <TabsTrigger value="at-risk">At-Risk Students</TabsTrigger>
            <TabsTrigger value="insights">Smart Insights</TabsTrigger>
          </TabsList>

          <TabsContent value="overview" className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
              <Card>
                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                  <CardTitle className="text-sm font-medium">Current GPA</CardTitle>
                  <span className="text-2xl">📚</span>
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold">{performanceData?.currentGPA?.toFixed(2)}</div>
                  <p className="text-xs text-muted-foreground">
                    {performanceData?.gradeTrend > 0 ? '📈 Improving' : '📉 Needs attention'}
                  </p>
                </CardContent>
              </Card>

              <Card>
                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                  <CardTitle className="text-sm font-medium">Attendance Rate</CardTitle>
                  <span className="text-2xl">📅</span>
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold">{performanceData?.attendanceRate?.toFixed(1)}%</div>
                  <p className="text-xs text-muted-foreground">
                    {performanceData?.attendanceTrend > 0 ? '📈 Improving' : '📉 Declining'}
                  </p>
                </CardContent>
              </Card>

              <Card>
                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                  <CardTitle className="text-sm font-medium">Risk Level</CardTitle>
                  <span className="text-2xl">{getRiskLevelIcon(performanceData?.riskLevel)}</span>
                </CardHeader>
                <CardContent>
                  <Badge className={getRiskLevelColor(performanceData?.riskLevel)}>
                    {performanceData?.riskLevel}
                  </Badge>
                  <p className="text-xs text-muted-foreground mt-2">
                    Based on performance trends
                  </p>
                </CardContent>
              </Card>

              <Card>
                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                  <CardTitle className="text-sm font-medium">Graduation Probability</CardTitle>
                  <span className="text-2xl">🎓</span>
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold">
                    {((performanceData?.predictions?.graduationProbability || 0) * 100).toFixed(1)}%
                  </div>
                  <Progress 
                    value={(performanceData?.predictions?.graduationProbability || 0) * 100} 
                    className="mt-2"
                  />
                </CardContent>
              </Card>
            </div>

            <Card>
              <CardHeader>
                <CardTitle>📊 Performance Trends</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  <div>
                    <div className="flex justify-between text-sm">
                      <span>Grade Trend</span>
                      <span className={performanceData?.gradeTrend > 0 ? 'text-green-600' : 'text-red-600'}>
                        {performanceData?.gradeTrend > 0 ? '+' : ''}{performanceData?.gradeTrend?.toFixed(2)}
                      </span>
                    </div>
                    <Progress value={Math.max(0, Math.min(100, (performanceData?.gradeTrend + 1) * 50))} />
                  </div>
                  <div>
                    <div className="flex justify-between text-sm">
                      <span>Attendance Trend</span>
                      <span className={performanceData?.attendanceTrend > 0 ? 'text-green-600' : 'text-red-600'}>
                        {performanceData?.attendanceTrend > 0 ? '+' : ''}{performanceData?.attendanceTrend?.toFixed(1)}%
                      </span>
                    </div>
                    <Progress value={Math.max(0, Math.min(100, (performanceData?.attendanceTrend + 10) * 5))} />
                  </div>
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="predictions" className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <Card>
                <CardHeader>
                  <CardTitle>🔮 Next Semester Predictions</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="space-y-4">
                    <div className="flex justify-between">
                      <span>Predicted GPA</span>
                      <span className="font-bold">{performanceData?.predictions?.nextSemesterGPA?.toFixed(2)}</span>
                    </div>
                    <div className="flex justify-between">
                      <span>Predicted Attendance</span>
                      <span className="font-bold">{performanceData?.predictions?.nextSemesterAttendance?.toFixed(1)}%</span>
                    </div>
                    <div className="flex justify-between">
                      <span>Honor Roll Probability</span>
                      <span className="font-bold">{((performanceData?.predictions?.honorRollProbability || 0) * 100).toFixed(1)}%</span>
                    </div>
                    <div className="flex justify-between">
                      <span>Warning Probability</span>
                      <span className="font-bold text-red-600">{((performanceData?.predictions?.academicWarningProbability || 0) * 100).toFixed(1)}%</span>
                    </div>
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>📈 Success Metrics</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="space-y-4">
                    <div>
                      <div className="flex justify-between text-sm mb-2">
                        <span>Graduation Probability</span>
                        <span>{((performanceData?.predictions?.graduationProbability || 0) * 100).toFixed(1)}%</span>
                      </div>
                      <Progress value={(performanceData?.predictions?.graduationProbability || 0) * 100} />
                    </div>
                    <div>
                      <div className="flex justify-between text-sm mb-2">
                        <span>Honor Roll Chance</span>
                      <span>{((performanceData?.predictions?.honorRollProbability || 0) * 100).toFixed(1)}%</span>
                      </div>
                      <Progress value={(performanceData?.predictions?.honorRollProbability || 0) * 100} />
                    </div>
                  </div>
                </CardContent>
              </Card>
            </div>
          </TabsContent>

          <TabsContent value="at-risk" className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <Card>
                <CardHeader>
                  <CardTitle>⚠️ Students Requiring Attention</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="space-y-4">
                    {atRiskStudents.map((student) => (
                      <div key={student.studentId} className="border rounded-lg p-4">
                        <div className="flex justify-between items-start mb-3">
                          <div>
                            <h4 className="font-semibold">{student.studentName}</h4>
                            <Badge className={getRiskLevelColor(student.riskLevel)}>
                              {getRiskLevelIcon(student.riskLevel)} {student.riskLevel}
                            </Badge>
                          </div>
                          <div className="text-sm text-gray-600">
                            GPA: {student.currentGPA?.toFixed(2)} | Attendance: {student.attendanceRate?.toFixed(1)}%
                          </div>
                        </div>
                        <div className="space-y-2">
                          <h5 className="font-medium text-sm mb-2">📋 Recommendations:</h5>
                          <ul className="text-sm space-y-1">
                            {student.recommendations?.map((rec, index) => (
                              <li key={index} className="flex items-start">
                                <span className="text-blue-500 mr-2">•</span>
                                <span>{rec}</span>
                              </li>
                            ))}
                          </ul>
                        </div>
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>
            </div>
          </TabsContent>

          <TabsContent value="insights" className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <Card>
                <CardHeader>
                  <CardTitle>🎯 Achievement Analysis</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="space-y-4">
                    <div className="p-4 bg-blue-50 rounded-lg">
                      <h4 className="font-semibold text-blue-800 mb-2">Current Status</h4>
                      <p className="text-blue-700">
                        {performanceData?.currentGPA >= 3.5 ? 'Excellent Performance' :
                         performanceData?.currentGPA >= 2.5 ? 'Good Performance' :
                         performanceData?.currentGPA >= 2.0 ? 'Satisfactory Performance' :
                         'Needs Improvement'}
                      </p>
                    </div>
                    <div className="p-4 bg-green-50 rounded-lg">
                      <h4 className="font-semibold text-green-800 mb-2">Next Steps</h4>
                      <p className="text-green-700">
                        {performanceData?.currentGPA >= 3.5 ? 'Consider advanced coursework or research opportunities' :
                         performanceData?.currentGPA >= 2.5 ? 'Focus on maintaining consistency and explore leadership roles' :
                         performanceData?.currentGPA >= 2.0 ? 'Seek academic support and improve study habits' :
                         'Immediate academic intervention required'}
                      </p>
                    </div>
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>📝 Personalized Recommendations</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="space-y-3">
                    {performanceData?.recommendations?.map((rec, index) => (
                      <div key={index} className="flex items-start p-3 bg-gray-50 rounded-lg">
                        <span className="text-purple-500 mr-3 text-lg">💡</span>
                        <span className="text-sm">{rec}</span>
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>
            </div>

            <Card>
              <CardHeader>
                <CardTitle>📊 Attendance Insights</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="p-4 bg-indigo-50 rounded-lg">
                  <h4 className="font-semibold text-indigo-800 mb-2">Attendance Analysis</h4>
                  <p className="text-indigo-700">
                    {performanceData?.attendanceRate >= 95 ? 'Perfect attendance - excellent commitment!' :
                     performanceData?.attendanceRate >= 85 ? 'Good attendance - keep it up!' :
                     performanceData?.attendanceRate >= 75 ? 'Satisfactory attendance - room for improvement' :
                     'Poor attendance - needs immediate attention'}
                  </p>
                </div>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
};

export default AIDashboard;
